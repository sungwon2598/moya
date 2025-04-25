import { Card, Button, Avatar } from '@/components/shared/ui';
import { Badge } from '@/components/shared/ui/badge';
import { CalendarDays, Users, MapPin } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface StudyCardProps {
  title: string;
  description: string;
  location: string;
  maxParticipants: number;
  currentParticipants: number;
  startDate: string;
  endDate: string;
  tags: string[];
  postId: string;
  creator: {
    name: string;
    avatar?: string;
  };
}

export const StudyCard = ({
  title,
  description,
  location,
  maxParticipants,
  currentParticipants,
  startDate,
  endDate,
  tags,
  creator,
  postId,
}: StudyCardProps) => {
  const navigate = useNavigate();
  return (
    <Card.Card
      className="w-full border-none transition-shadow duration-200 hover:shadow-lg"
      onClick={() => navigate(`/study/${postId}`)}>
      <Card.CardHeader>
        <div className="flex items-center justify-between">
          <Card.CardTitle className="text-xl">{title}</Card.CardTitle>
          <Avatar.Avatar>
            <Avatar.AvatarImage src={creator.avatar} />
            <Avatar.AvatarFallback>{creator.name[0]}</Avatar.AvatarFallback>
          </Avatar.Avatar>
        </div>
        <Card.CardDescription className="line-clamp-2">{description}</Card.CardDescription>
      </Card.CardHeader>
      <Card.CardContent>
        <div className="space-y-4">
          <div className="text-muted-foreground flex items-center gap-2 text-sm">
            <CalendarDays className="h-4 w-4" />
            <span>
              {startDate} ~ {endDate}
            </span>
          </div>
          <div className="text-muted-foreground flex items-center gap-2 text-sm">
            <MapPin className="h-4 w-4" />
            <span>{location}</span>
          </div>
          <div className="text-muted-foreground flex items-center gap-2 text-sm">
            <Users className="h-4 w-4" />
            <span>
              {currentParticipants} / {maxParticipants}명
            </span>
          </div>
          <div className="flex flex-wrap gap-2">
            {tags.map((tag) => (
              <Badge key={tag} variant="secondary">
                {tag}
              </Badge>
            ))}
          </div>
        </div>
      </Card.CardContent>
      <Card.CardFooter className="flex justify-between">
        <Button variant="outline">상세보기</Button>
        <Button>참여하기</Button>
      </Card.CardFooter>
    </Card.Card>
  );
};
